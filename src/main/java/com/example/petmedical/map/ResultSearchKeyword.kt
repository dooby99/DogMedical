package com.example.petmedical.map

class ResultSearchKeyword(val documents: List<Place>)

data class Place(
    val place_name: String,         // 장소명
    val address_name: String,       // 전체 번지 주소
    val road_address_name: String,  // 전체 도로명 주소
    val x: String,                  // X 좌표값 or longitude
    val y: String,                  // Y 좌표값 or latitude
//    val radius: Int,                // 중심좌표까지의 거리. 단 x,y 파라미터 준 경우에만 사용 가능, 단위는 미터
    val distance: String
)