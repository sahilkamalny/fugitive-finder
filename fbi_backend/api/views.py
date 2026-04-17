import requests
import json
import uuid
from django.views.decorators.csrf import csrf_exempt
from .models import AppUser
from django.http import JsonResponse

FBI_API_URL = "https://api.fbi.gov/wanted/v1/list"

def get_wanted_persons(request):
    page = request.GET.get("page", 1)

    try:
        response = requests.get(FBI_API_URL, params={"page": page})
        data = response.json()

        transformed_items = []

        for item in data.get("items", []):
            transformed_items.append({
                "uid": item.get("uid"),
                "title": item.get("title"),
                "description": item.get("description"),
                "status": item.get("status"),
                "sex": item.get("sex"),
                "race": item.get("race"),
                "nationality": item.get("nationality"),
                "hair": item.get("hair"),
                "eyes": item.get("eyes"),
                "rewardText": item.get("reward_text"),
                "rewardMin": item.get("reward_min"),
                "rewardMax": item.get("reward_max"),
                "subjects": item.get("subjects", []),
                "fieldOffices": item.get("field_offices", []),
                "images": [img.get("original") for img in item.get("images", [])],
                "publication": item.get("publication")
            })

        return JsonResponse({
            "total": data.get("total"),
            "items": transformed_items
        })

    except Exception as e:
        return JsonResponse({"error": str(e)}, status=500)
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse
import json
from .models import AppUser

@csrf_exempt
def register(request):
    if request.method == "POST":
        try:
            data = json.loads(request.body)

            first_name = data.get("firstName")
            last_name = data.get("lastName")
            username = data.get("username")
            email = data.get("email")
            password = data.get("password")

            if not email or not username:
                return JsonResponse({"error": "Missing fields"}, status=400)

            if AppUser.objects.filter(email=email).exists():
                return JsonResponse({"error": "User already exists"}, status=400)

            user = AppUser.objects.create(
                uid=username,
                username=username,
                first_name=first_name,
                last_name=last_name,
                email=email,
                password=password
            )

            return JsonResponse({
                "message": "User created",
                "username": user.username
            })

        except Exception as e:
            print("ERROR:", e)  # 👈 THIS WILL SHOW ERROR IN TERMINAL
            return JsonResponse({"error": str(e)}, status=500)

    return JsonResponse({"error": "Invalid request method"}, status=400)
@csrf_exempt
def login(request):
    if request.method == "POST":
        data = json.loads(request.body)

        try:
            user = AppUser.objects.get(
                email=data.get("email"),
                password=data.get("password")
            )

            return JsonResponse({
                "uid": user.uid,
                "username": user.username,
                "email": user.email,
                "savedTargets": user.saved_targets
            })

        except AppUser.DoesNotExist:
            return JsonResponse({"error": "Invalid login"}, status=401)
@csrf_exempt
def save_target(request):
    if request.method == "POST":
        data = json.loads(request.body)

        try:
            user = AppUser.objects.get(uid=data.get("uid"))
            target_id = data.get("targetId")

            if target_id not in user.saved_targets:
                user.saved_targets.append(target_id)
                user.save()

            return JsonResponse({"message": "Saved"})

        except AppUser.DoesNotExist:
            return JsonResponse({"error": "User not found"}, status=404)