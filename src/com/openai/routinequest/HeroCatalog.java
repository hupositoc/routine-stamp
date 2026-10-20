package com.openai.routinequest;

public final class HeroCatalog {
    private HeroCatalog() {}

    public static final Hero[] HEROES = new Hero[] {
            new Hero("jan", "1월", "새싹 수호자", "리프", "숲의 시작을 여는 수호자", R.drawable.hero_jan, new String[]{"짚신", "튜닉", "주머니", "후드", "장갑", "지팡이", "보호구", "망토", "희귀 장식", "전설 완성"}),
            new Hero("feb", "2월", "보랏빛 마도사", "루나", "작은 결심을 마법으로 바꾸는 소녀", R.drawable.hero_feb, new String[]{"부적", "로브", "약초병", "마녀 모자", "별 장갑", "수정봉", "숄", "망토", "비약", "전설 완성"}),
            new Hero("mar", "3월", "숲의 궁수", "베리단", "꾸준함으로 화살을 벼리는 궁수", R.drawable.hero_mar, new String[]{"가죽 신발", "튜닉", "화살통", "후드", "궁수 장갑", "단검", "흉갑", "레인저 망토", "장궁", "전설 완성"}),
            new Hero("apr", "4월", "새벽의 성녀", "세라", "맑은 아침처럼 단정한 치유자", R.drawable.hero_apr, new String[]{"묵주", "로브", "약초 가방", "베일", "금실 장갑", "성직봉", "은 장식", "성직 망토", "티아라", "전설 완성"}),
            new Hero("may", "5월", "봄의 요정", "플로라", "꽃과 함께 자라는 활기찬 요정", R.drawable.hero_may, new String[]{"꽃팔찌", "원피스", "씨앗 주머니", "꽃관", "나비 장갑", "마법봉", "잎 장식", "꽃망토", "화관", "전설 완성"}),
            new Hero("jun", "6월", "푸른 기사", "아론", "습관을 갑옷으로 삼는 기사", R.drawable.hero_jun, new String[]{"부츠", "튜닉", "허리띠", "목 보호대", "건틀릿", "훈련검", "흉갑", "기사 망토", "방패", "전설 완성"}),
            new Hero("jul", "7월", "모험가 탐험꾼", "카이", "지도 위를 걷는 뜨거운 탐험가", R.drawable.hero_jul, new String[]{"밧줄", "여행복", "지도통", "두건", "등산화", "나침반", "조끼", "탐험 망토", "고글", "전설 완성"}),
            new Hero("aug", "8월", "숲의 레인저", "리프", "꾸준함으로 숲의 영웅으로 자라는 소년", R.drawable.hero_aug, new String[]{"새싹 장식", "견습복", "허리 주머니", "레인저 모자", "궁수 장갑", "단검", "숲 망토", "깃 장식", "마법 장궁", "영웅 완성"}),
            new Hero("sep", "9월", "여우 도적", "유나", "재치 있게 성과를 쌓는 여우 도적", R.drawable.hero_sep, new String[]{"붉은 끈", "경량복", "도구집", "여우 후드", "장화", "쌍단검", "어깨 보호구", "그림자 망토", "은장 단검", "전설 완성"}),
            new Hero("oct", "10월", "호박 흑마도사", "잭", "으스스하지만 성실한 밤의 마도사", R.drawable.hero_oct, new String[]{"짚신", "마도포", "촛불 주머니", "마녀 모자", "검은 장갑", "호박 랜턴", "보호 숄", "밤안개 망토", "마도 지팡이", "전설 완성"}),
            new Hero("nov", "11월", "서리 현자", "노엘", "차분함으로 성장을 기록하는 현자", R.drawable.hero_nov, new String[]{"잉크병", "학도 로브", "서책 가방", "서리 두건", "보온 장갑", "마도서", "푸른 케이프", "서리 망토", "빙정 서책", "전설 완성"}),
            new Hero("dec", "12월", "겨울 선물장수", "산타르", "한 해의 마무리를 축하하는 축제 영웅", R.drawable.hero_dec, new String[]{"방울", "작업복", "선물 주머니", "털모자", "방한 장갑", "랜턴", "어깨 망토", "축제 외투", "별 장식", "전설 완성"})
    };

    public static Hero heroForMonth(int monthZeroBased) {
        return HEROES[Math.floorMod(monthZeroBased, HEROES.length)];
    }
}

class Hero {
    public final String id;
    public final String monthLabel;
    public final String role;
    public final String name;
    public final String tagline;
    public final int imageRes;
    public final String[] rewards;

    Hero(String id, String monthLabel, String role, String name, String tagline, int imageRes, String[] rewards) {
        this.id = id;
        this.monthLabel = monthLabel;
        this.role = role;
        this.name = name;
        this.tagline = tagline;
        this.imageRes = imageRes;
        this.rewards = rewards;
    }
}
