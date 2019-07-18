import datetime
import os
from typing import Any, Iterable

import requests
from invoke import task


# TODO add tests
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


@task
def mongo_local(ctx):
    """
    Start local mongo db in docker
    """
    ctx.run(docker_compose("up mongo"))


@task(help={"host": "MongoDB host",
            "username": "MongoDB username",
            "password": "MongoDB password",
            "database": "MongoDB database to backup (optional)",
            "directory": "MongoDB backup directory (optional)"})
def mongo_backup(ctx, host, username, password, database="prod", directory="/tmp/mongo-backup"):
    """
    Backup MongoDB database locally.
    """
    backup_dir = os.path.join(directory, datetime.datetime.now().strftime("%Y-%m-%d_%H-%M-%S"))

    print(f"Creating backup for database {database} in {backup_dir}")
    ctx.run(docker("run", f"--volume {backup_dir}:{backup_dir}", "mongo", "mongodump",
                   "--ssl",
                   "--authenticationDatabase admin",
                   f"--host {host}",
                   f"--username {username}",
                   f"--password {password}",
                   f"--db {database}",
                   f"--out {backup_dir}"))


@task(help={"host": "MongoDB host",
            "username": "MongoDB username",
            "password": "MongoDB password",
            "backup-dir": "MongoDB backup directory"})
def mongo_restore(ctx, host, username, password, backup_dir):
    """
    Restore MongoDB database from a local backup.
    """
    print(f"Restoring backup in {backup_dir}")
    ctx.run(docker("run", f"--volume {backup_dir}:{backup_dir}", "mongo", "mongorestore",
                   "--ssl",
                   "--authenticationDatabase admin",
                   f"--host {host}",
                   f"--username {username}",
                   f"--password {password}",
                   f"--dir {backup_dir}"))


def gradle(*arguments: str) -> str:
    return f"./gradlew {join(arguments)}"


def docker(*arguments: str) -> str:
    return f"docker {join(arguments)}"


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
