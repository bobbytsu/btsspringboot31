package com.juaracoding.pcmspringboot31.coretan;

import com.juaracoding.pcmspringboot31.controller.TestAuthController;
import com.juaracoding.pcmspringboot31.dto.validation.ValMenuDTO;
import com.juaracoding.pcmspringboot31.util.GenerateData;
import com.juaracoding.pcmspringboot31.util.GenerateExcelMenu;

import java.util.ArrayList;
import java.util.List;

public class GenExcel {

    public static void main(String[] args) {
        
        GenerateData data = new  GenerateData();
        Long longCount = 3L;
        List<ValMenuDTO> list=new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            longCount++;
            String nama = data.genDataAlfaNumeric(3,20);
            String path = "/"+nama.replaceAll(" ","").trim().toLowerCase();
            String deskripsi = data.genDataAlfaNumeric(20,255);
            String kodeMenu = getKodeMenu(String.valueOf(longCount));
            ValMenuDTO menu = new ValMenuDTO();
            menu.setNama(nama);
            menu.setPath(path);
            menu.setDeskripsi(deskripsi);
            menu.setKodeMenu(kodeMenu);
            list.add(menu);
        }
        String [] kolomMenu = {"NAMA","PATH","DESKRIPSI","KODE_MENU"};
        GenerateExcelMenu.generateDataExcel(list,kolomMenu);
    }

    private static String getKodeMenu(String count){
//        menu = menuRepo.findTop1ByOrderByIdDesc().orElse(null);
        String kode = "";
        if(count.length()<3){
            int paddingLength = 3 - count.length();
            String padding = "0".repeat(paddingLength);
            kode = padding + count;
        }else{
            kode = count;
        }
        return kode;
    }
}
