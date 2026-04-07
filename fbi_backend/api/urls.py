from django.urls import path
from .views import get_wanted_persons

urlpatterns = [
    path("wanted/", get_wanted_persons),
]