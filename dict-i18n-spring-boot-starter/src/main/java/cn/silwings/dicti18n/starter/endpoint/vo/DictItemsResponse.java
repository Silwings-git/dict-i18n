package cn.silwings.dicti18n.starter.endpoint.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DictItemsResponse {
    /**
     * 字典项
     */
    private Map<String, List<DictItemVO>> items = new HashMap<>();

    public static DictItemsResponse empty() {
        return new DictItemsResponse(Collections.emptyMap());
    }
}
