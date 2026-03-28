package com.backend.service;


import java.util.List;

import com.backend.domain.Perfil;
import com.backend.dto.PerfilRequestDto;
import com.backend.dto.PerfilResponseDto;




public interface IPerfilService  {


    List<PerfilResponseDto> findAll();
    
    Perfil save(Perfil perfil);     

    PerfilResponseDto findById(Long id);

    void deleteById(Long id);

    PerfilResponseDto update(Long id, PerfilRequestDto dto);    
}
