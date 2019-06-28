from typing import Iterable

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


def gradle(*arguments: str) -> str:
    return f"./gradlew {join(arguments)}"


def docker_compose(*arguments: str) -> str:
    return f"docker-compose {join(arguments)}"


def heroku(*arguments: str) -> str:
    return f"heroku {join(arguments)}"


def join(arguments: Iterable[str]) -> str:
    return " ".join(arguments)
