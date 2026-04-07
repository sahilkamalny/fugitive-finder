import requests
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