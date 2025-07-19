package cn.silwings.dicti18n.starter.endpoint.vo;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DictItemVO {

    /**
     * Dictionary entry encoding
     */
    private String code;

    /**
     * Dictionary entry description
     */
    private String desc;
}