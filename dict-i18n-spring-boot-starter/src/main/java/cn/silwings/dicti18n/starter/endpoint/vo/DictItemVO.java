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
     * 字典项编码
     */
    private String code;

    /**
     * 字典项描述
     */
    private String desc;
}