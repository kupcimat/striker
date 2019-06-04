from typing import Iterable

from invoke import task


@task
def build_image(ctx):
    """
    Build docker image locally
    """
    ctx.run(gradle("jibDockerBuild"))


@task(help={"username": "Heroku docker registry username",
            "password": "Heroku docker registry password"})
def deploy_heroku(ctx, username, password):
    """
    Deploy docker image on heroku
    """
    ctx.run(gradle("jib", f"-Djib.to.auth.username={username}", f"-Djib.to.auth.password={password}"))
    ctx.run(heroku("container:release web", "--app=striker-vn"))


def gradle(*arguments: str) -> str:
    return f"./gradlew {join(arguments)}"


def docker(*arguments: str) -> str:
    return f"docker {join(arguments)}"


def heroku(*arguments: str) -> str:
    return f"heroku {join(arguments)}"


def join(arguments: Iterable[str]) -> str:
    return " ".join(arguments)
