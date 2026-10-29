package com.eseict.kaist.service.fac;

import com.eseict.kaist.data.dto.FavoriteBodyType;
import com.eseict.kaist.data.dto.SensorResponse;
import com.eseict.kaist.data.dto.SensorType;
import com.eseict.kaist.data.vo.fac.TblBuilding;
import com.eseict.kaist.data.vo.fac.TblCctv;
import com.eseict.kaist.data.vo.fac.TblFireSensor;
import com.eseict.kaist.data.vo.fac.TblGasSensor;
import com.eseict.kaist.repository.BuildingRepository;
import com.eseict.kaist.repository.CctvRepository;
import com.eseict.kaist.repository.FireSensorRepository;
import com.eseict.kaist.repository.GasSensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacService {

    private final CctvRepository cctvRepository;
    private final BuildingRepository buildingRepository;
    private final FireSensorRepository fireSensorRepository;
    private final GasSensorRepository gasSensorRepository;


    public FavoriteBodyType getFavorites() {
        List<TblCctv> cctvList = cctvRepository.findByFavoriteYnIs("1");
        List<TblBuilding> buildingList = buildingRepository.findByFavoriteYnIs("1");

        FavoriteBodyType favoriteBodyType = new FavoriteBodyType();
        favoriteBodyType.setFavorites(cctvList.stream().map(TblCctv::getStreamId).collect(Collectors.joining(",")));
        favoriteBodyType.setBuildFavorites(buildingList.stream().map(TblBuilding::getId).collect(Collectors.joining(",")));

        return favoriteBodyType;
    }

    public boolean updateFavorites(FavoriteBodyType ids) {
        int updateResult = 0;
        int buildingUpdateResult = 0;

        if(!ids.getBuildFavorites().isEmpty()){
            buildingUpdateResult = buildingRepository.updateFavorites(Arrays.asList(ids.getBuildFavorites().split(",")));
        }else{
            buildingUpdateResult = 1;
        }

        if(!ids.getFavorites().isEmpty()){
            updateResult = cctvRepository.updateFavorites(Arrays.asList(ids.getFavorites().split(",")));
        }else{
            updateResult = 1;
        }


        return (buildingUpdateResult>0  && updateResult>0);
    }

    public SensorResponse getSensors(){
        List<TblFireSensor> allFires = fireSensorRepository.findAll();
        List<TblGasSensor> allGases = gasSensorRepository.findAll();
        List<TblCctv> flameCctvs = cctvRepository.findByPlcType("3");

        List<SensorType> fireSensors = allFires.stream().map(item -> new SensorType(item.getSensorId(), item.getSensorArea(), item.getSensorBuilding())).collect(Collectors.toList());
        List<SensorType> gasSensors = allGases.stream().map(item-> new SensorType(item.getSensorId(), item.getSensorNm(), item.getSensorBuilding())).collect(Collectors.toList());
        List<SensorType> flameSensors = flameCctvs.stream().map(item -> new SensorType(item.getStreamId(), item.getCctvNm(), item.getBuilding())).collect(Collectors.toList());

        return new SensorResponse(fireSensors, gasSensors, flameSensors);
    }

}
