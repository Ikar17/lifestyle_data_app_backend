package com.example.lifestyle_data_app.service;

import com.example.lifestyle_data_app.model.Address;
import com.example.lifestyle_data_app.model.Comunne;
import com.example.lifestyle_data_app.model.District;
import com.example.lifestyle_data_app.model.Voivodeship;
import com.example.lifestyle_data_app.repository.AddressRepository;
import com.example.lifestyle_data_app.repository.ComunneRepository;
import com.example.lifestyle_data_app.repository.DistrictRepository;
import com.example.lifestyle_data_app.repository.VoivodeshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private VoivodeshipRepository voivodeshipRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private ComunneRepository comunneRepository;

    public List<Voivodeship> getVoivodeships(){
        return voivodeshipRepository.findAll();
    }

    public List<District> getDistrictsByVoivodeship(String voivodeshipName){
        return districtRepository.findAllByVoivodeshipName(voivodeshipName);
    }

    public List<Comunne> getComunnesByDistrict(String districtName){
        return comunneRepository.findAllByDistrict_Name(districtName);
    }

    public Address addAddress(String voivodeshipName, String districtName, String comunneName){
        try{
            Voivodeship voivodeship = voivodeshipRepository.findByName(voivodeshipName);
            District district = districtRepository.findByName(districtName);
            Comunne comunne = comunneRepository.findByName(comunneName);
            if(voivodeship==null || district==null || comunne==null) return null;

            Address address = new Address();
            address.setVoivodeship(voivodeship);
            address.setDistrict(district);
            address.setComunne(comunne);

            return address;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
