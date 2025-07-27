package cn.silwings.dicti18n.starter.endpoint.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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