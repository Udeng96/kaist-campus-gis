package com.eseict.kaist.service.fac;


import com.eseict.kaist.data.vo.fac.TblBuilding;
import com.eseict.kaist.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public List<TblBuilding> getBuildings() {
        List<TblBuilding> buildings = buildingRepository.findAll();


        List<Character> areaOrder = Arrays.asList('N', 'W', 'E'); // 순서 우선순위


        buildings.sort((a, b) -> {
            String idA = a.getId();
            String idB = b.getId();

            // 1️⃣ 구역 접두어 비교 (N > W > E)
            char prefixA = idA.charAt(0);
            char prefixB = idB.charAt(0);
            int cmpArea = Integer.compare(areaOrder.indexOf(prefixA), areaOrder.indexOf(prefixB));
            if (cmpArea != 0) return cmpArea;

            // 2️⃣ 숫자 부분 추출
            // "16-2" -> [16, 2], "5" -> [5, 0]
            int[] numsA = parseNumbers(idA.substring(1));
            int[] numsB = parseNumbers(idB.substring(1));

            // 메인 숫자 비교
            int cmpMain = Integer.compare(numsA[0], numsB[0]);
            if (cmpMain != 0) return cmpMain;

            // 서브 숫자 비교 (없으면 0으로 처리)
            return Integer.compare(numsA[1], numsB[1]);
        });

        return buildings;
    }

    private static int[] parseNumbers(String s) {
        String[] parts = s.split("-");
        int main = parseSafeInt(parts[0]);
        int sub = (parts.length > 1) ? parseSafeInt(parts[1]) : 0;
        return new int[]{main, sub};
    }

    private static int parseSafeInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0; // 숫자 아니면 0 처리
        }
    }
}
