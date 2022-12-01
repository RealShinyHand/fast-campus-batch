package com.fastcampus.hellospringbatch.core.service;

import com.fastcampus.hellospringbatch.core.dto.PlayerDto;
import com.fastcampus.hellospringbatch.core.dto.PlayerSalaryDto;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class PlayerSalaryService {
    public PlayerSalaryDto calcSalary(PlayerDto playerDto){
        //선수의 나이 * 백만
        int salary = (Year.now().getValue() - playerDto.getBirthYear()) * 1000000;
        PlayerSalaryDto playerSalaryDto = PlayerSalaryDto.of(playerDto,salary);
        return  playerSalaryDto;
    }
}
