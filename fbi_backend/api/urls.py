from django.urls import path
from .views import get_wanted_persons, register, login, save_target, proxy_image

urlpatterns = [
    path("wanted/", get_wanted_persons),
    path("register/", register),
    path("login/", login),
    path("save-target/", save_target),
    path("image", proxy_image),
]