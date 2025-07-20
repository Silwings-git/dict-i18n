package cn.silwings.dicti18n.loader.parser;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DictInfo {

    /**
     * Dictionary key,
     * Usually composed of dictionary name and dictionary code
     */
    private String dictKey;

    /**
     * Dictionary description,
     */
    private String dictDesc;

    public boolean isValid() {
        return null != this.dictKey && null != this.dictDesc && !this.dictDesc.isEmpty();
    }
}
