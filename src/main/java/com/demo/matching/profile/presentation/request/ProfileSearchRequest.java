package com.demo.matching.profile.presentation.request;

import com.demo.matching.profile.domain.enums.ProfileSortType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProfileSearchRequest(
        @NotNull(message = "정렬 속성은 기본값인 빈값 이거나 반드시 지정해야 합니다.")
        ProfileSortType profileSortType,

        @NotNull(message = "페이지는 필수입니다.")
        @Min(value = 0, message = "페이지는 0 이상이어야 합니다.")
        Integer page,

        @NotNull(message = "사이즈는 필수입니다.")
        @Min(value = 1, message = "사이즈는 1 이상이어야 합니다.") @Max(value = 10, message = "최대 사이즈는 10 이하여야 합니다.")
        Integer size
) {}
