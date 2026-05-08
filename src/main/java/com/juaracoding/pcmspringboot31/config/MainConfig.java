package com.juaracoding.pcmspringboot31.config;


import com.juaracoding.pcmspringboot31.dto.validation.ValAksesDTO;
import com.juaracoding.pcmspringboot31.model.Akses;
import com.juaracoding.pcmspringboot31.model.Menu;
import com.juaracoding.pcmspringboot31.security.Crypto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Configuration
public class MainConfig{

    @Autowired
    private Environment env;

    @Primary
    @Bean
    public DataSource getDataSourceBuilder(){
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(Crypto.performDecrypt(env.getProperty("spring.datasource.url")));
        dataSourceBuilder.username(Crypto.performDecrypt(env.getProperty("spring.datasource.username")));
        dataSourceBuilder.password(Crypto.performDecrypt(env.getProperty("spring.datasource.password")));
        dataSourceBuilder.driverClassName(env.getProperty("spring.datasource.driver-class-name"));
        return dataSourceBuilder.build();
    }

    @Bean
    public Random getRandom(){
        return new Random();
    }

    @Bean
    public ModelMapper getModelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        // 1. Ubah Matching Strategy ke STRICT agar 'menuId' tidak salah masuk ke 'id'
        modelMapper.getConfiguration().setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STRICT);
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        // 2. Buat Converter sederhana dari Long menjadi Menu
        Converter<Long, Menu> longToMenuConverter = new AbstractConverter<Long, Menu>() {
            @Override
            protected Menu convert(Long sourceId) {
                if (sourceId == null) {
                    return null;
                }
                Menu m = new Menu();
                m.setId(sourceId);
                return m;
            }
        };
        // 3. Daftarkan Converter secara global
        modelMapper.addConverter(longToMenuConverter);
        // 4. Beritahu ModelMapper untuk memetakan menuId ke menus
        modelMapper.typeMap(ValAksesDTO.class, Akses.class).addMappings(mapper -> {
            mapper.map(ValAksesDTO::getMenuId, Akses::setMenus);
        });

        return modelMapper;
    }
}
