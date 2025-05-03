package com._data._data.community.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostWithAuthorProfileListDto {

    private List<PostWithAuthorProfileDto> posts;
}
