#!/bin/bash
curl -i -H "Content-Type: Application/json" -H "Authorization: Bearer eyJrIjoiaTNqN1hhY0I0d2NvbnBFbzVJRFRVZUE4NGZjZU5icnoiLCJuIjoiR3JhZmFuYSBEZXBsb3kgU2NyaXB0IiwiaWQiOjF9" -X POST http://34.249.229.63:3000/api/dashboards/db -d @Dashboard.json
