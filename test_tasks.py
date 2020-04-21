import pytest

import tasks


@pytest.mark.parametrize("arguments, expected", [
    ([], ""),
    (["app"], "app"),
    (["app", "--opt"], "app --opt"),
    (["app", "--opt", "arg"], "app --opt arg")
])
def test_join(arguments, expected):
    assert tasks.join(arguments) == expected
