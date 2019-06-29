from typing import Any, Iterable

import requests
from invoke import task


# TODO add tests
@task
def start_mongo(ctx):
    """
    Start local mongo db in docker
    """
    ctx.run(docker_compose("up mongo"))


@task
def build_image(ctx):
    """
    Build docker image locally
    """
    ctx.run(gradle("jibDockerBuild"))


@task(build_image)
def deploy_local(ctx):
    """
    Build docker image and run it locally
    """
    ctx.run(docker_compose("up"))


@task(help={"username": "Heroku docker registry username",
            "password": "Heroku docker registry password",
            "heroku-app": "Heroku application name (optional)"})
def deploy_heroku(ctx, username, password, heroku_app="striker-vn"):
    """
    Deploy docker image on heroku
    """
    ctx.run(gradle("jib",
                   f"-Djib.to.auth.username={username}",
                   f"-Djib.to.auth.password={password}"))
    ctx.run(heroku("container:release web", f"--app={heroku_app}"))


@task(help={"username": "Application test user username",
            "password": "Application test user password",
            "heroku-app": "Heroku application name (optional)"})
def run_tests(ctx, username, password, heroku_app="striker-vn"):
    """
    Run integration tests
    """
    ctx.run(gradle("test",
                   f"-DserverUrl=https://{heroku_app}.herokuapp.com",
                   f"-Dusername={username}",
                   f"-Dpassword={password}"))


@task(help={"heroku-app": "Heroku application name (optional)"})
def run_health_check(ctx, heroku_app="striker-vn"):
    """
    Run application health check
    """
    health = get_json(f"https://{heroku_app}.herokuapp.com/actuator/health")
    info = get_json(f"https://{heroku_app}.herokuapp.com/actuator/info")

    print(f"Status  = {safe_get(health, 'status')}")
    print(f"Version = {safe_get(info, 'build', 'version')} ({safe_get(info, 'build', 'time')})")


def gradle(*arguments: str) -> str:
    return f"./gradlew {join(arguments)}"


def docker_compose(*arguments: str) -> str:
    return f"docker-compose {join(arguments)}"


def heroku(*arguments: str) -> str:
    return f"heroku {join(arguments)}"


def join(arguments: Iterable[str]) -> str:
    return " ".join(arguments)


def get_json(url: str) -> dict:
    response = requests.get(url)
    print(f"GET {url} ({response.status_code} {response.reason})")
    return response.json()


def safe_get(json: Any, *path: str) -> Any:
    if len(path) == 0:
        return json
    else:
        if path[0] in json:
            return safe_get(json[path[0]], *path[1:])
        else:
            return safe_get("unknown")
