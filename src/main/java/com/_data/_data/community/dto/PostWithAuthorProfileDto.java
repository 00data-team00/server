package com._data._data.community.dto;

public record PostWithAuthorProfileDto(
    PostDto post,
    PostAuthorProfileDto authorProfile
) {}