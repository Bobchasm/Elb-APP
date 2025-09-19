package com.tju.elm_bk.service;

import com.tju.elm_bk.dto.PersonUpdateDTO;
import com.tju.elm_bk.entity.Person;
import jakarta.validation.Valid;

public interface PersonService {
    Person getPersonByUserId(Long id);
    void addPerson(Person person);

    Person updatePerson(@Valid PersonUpdateDTO updateDTO);
}
