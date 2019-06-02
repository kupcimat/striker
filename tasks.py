from typing import Iterable

from invoke import task


@task
def build_image(ctx):
    """
    Build docker image
    """
    ctx.run(gradle("jibDockerBuild"))


@task(build_image)
def deploy_heroku(ctx):
    """
    Deploy docker image on heroku
    """
    # TODO replace with direct upload from jib
    ctx.run(heroku("container:login"))
    ctx.run(docker("push registry.heroku.com/striker-vn/web"))
    ctx.run(heroku("container:release web", "--app=striker-vn"))


def gradle(*arguments: str) -> str:
    return f"./gradlew {join(arguments)}"


def docker(*arguments: str) -> str:
    return f"docker {join(arguments)}"


def heroku(*arguments: str) -> str:
    return f"heroku {join(arguments)}"


def join(arguments: Iterable[str]) -> str:
    return " ".join(arguments)
