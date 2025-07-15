package cn.silwings.dicti18n.starter.endpoint.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DictItemRequest {
    /**
     * 需要查询的字典名称
     */
    private List<String> dictNames;

    /**
     * 需要查询的语言,若不指定将使用默认值
     */
    private String lang;
}
