package me.zyee.ui;

import java.util.Collection;

/**
 * @author yee
 * @date 2018/11/9
 */
interface DataChangedProvider<Element> {
    void setData(Element[] elements);

    void setData(Collection<Element> elements);
}
