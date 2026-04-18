import requests
import json
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse
from .models import AppUser

FBI_API_URL = "https://api.fbi.gov/wanted/v1/list"


# =========================
# FBI DATA ENDPOINT
# =========================
def get_wanted_persons(request):
    page = request.GET.get("page", 1)
    page_size = request.GET.get("pageSize", 50)

    try:
        response = requests.get(FBI_API_URL, params={"page": page, "pageSize": page_size})
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
                "images": [img.get("original") for img in item.get("images", []) if img.get("original")],
                "publication": item.get("publication")
            })

        return JsonResponse({
            "total": data.get("total"),
            "items": transformed_items
        })

    except Exception as e:
        return JsonResponse({"error": str(e)}, status=500)


# =========================
# REGISTER
# =========================
@csrf_exempt
def register(request):
    if request.method != "POST":
        return JsonResponse({"error": "POST required"}, status=405)

    try:
        data = json.loads(request.body)

        first_name = data.get("firstName")
        last_name = data.get("lastName")
        username = data.get("username")
        email = data.get("email")
        password = data.get("password")

        if not all([first_name, last_name, username, email, password]):
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
            "uid": user.uid,
            "username": user.username,
            "firstName": user.first_name,
            "lastName": user.last_name,
            "email": user.email,
            "savedTargetIds": user.saved_targets
        }, status=201)

    except Exception as e:
        print("REGISTER ERROR:", e)
        return JsonResponse({"error": str(e)}, status=500)


# =========================
# LOGIN
# =========================
@csrf_exempt
def login(request):
    if request.method != "POST":
        return JsonResponse({"error": "POST required"}, status=405)

    try:
        data = json.loads(request.body)

        email = data.get("email")
        password = data.get("password")

        user = AppUser.objects.get(email=email, password=password)

        return JsonResponse({
            "uid": user.uid,
            "username": user.username,
            "firstName": user.first_name,
            "lastName": user.last_name,
            "email": user.email,
            "savedTargets": user.saved_targets
        })

    except AppUser.DoesNotExist:
        return JsonResponse({"error": "Invalid credentials"}, status=401)

    except Exception as e:
        print("LOGIN ERROR:", e)
        return JsonResponse({"error": str(e)}, status=500)


# =========================
# SAVE TARGET
# =========================
@csrf_exempt
def save_target(request):
    if request.method != "POST":
        return JsonResponse({"error": "POST required"}, status=405)

    try:
        data = json.loads(request.body)

        uid = data.get("uid")
        target_id = data.get("targetId")

        user = AppUser.objects.get(uid=uid)

        if target_id and target_id not in user.saved_targets:
            user.saved_targets.append(target_id)
            user.save()

        return JsonResponse({"message": "Saved", "savedTargets": user.saved_targets})

    except AppUser.DoesNotExist:
        return JsonResponse({"error": "User not found"}, status=404)

    except Exception as e:
        print("SAVE TARGET ERROR:", e)
        return JsonResponse({"error": str(e)}, status=500)