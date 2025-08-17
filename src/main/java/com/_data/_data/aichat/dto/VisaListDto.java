package com._data._data.aichat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VisaListDto {

    List<TopicDto> employmentVisa;

    List<TopicDto> jobSearchingVisa;
}
