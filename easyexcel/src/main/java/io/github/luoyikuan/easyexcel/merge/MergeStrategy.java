package io.github.luoyikuan.easyexcel.merge;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;

/**
 * 合并策略
 * 
 * @author lyk
 */
public class MergeStrategy<T> implements CellWriteHandler {

    private List<T> data;
    private boolean dataEmpty;
    private Set<Integer> mergeIndex;
    private List<int[]> mergeRange;

    public MergeStrategy(List<T> data, Set<Integer> mergeIndex) {
        this.data = data;
        this.mergeIndex = mergeIndex;
        this.dataEmpty = CollectionUtils.isEmpty(data);
        this.mergeRange = new ArrayList<>();

        if (!this.dataEmpty) {
            Field field = excelIdField(data.get(0));
            field.setAccessible(true);

            for (int i = 0; i < this.data.size(); i++) {
                int[] mr = new int[2];
                mr[0] = i;
                Object v0 = ReflectionUtils.getField(field, this.data.get(i));
                for (int j = i + 1; j < this.data.size(); j++) {
                    Object v1 = ReflectionUtils.getField(field, this.data.get(j));
                    if (v0.equals(v1)) {
                        mr[1] = j;
                    } else {
                        break;
                    }
                }
                if (mr[0] < mr[1]) {
                    mergeRange.add(mr);
                    i = mr[1];
                }
            }
        }
    }

    private Field excelIdField(T t) {
        Class<?> clazz = t.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelId.class)) {
                return field;
            }
        }
        throw new RuntimeException("缺少@ExcelId");
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
            List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

        if (isHead || dataEmpty || !mergeIndex.contains(cell.getColumnIndex())) {
            return;
        }

        for (int[] mr : mergeRange) {
            if (mr[1] == relativeRowIndex) {
                CellRangeAddress cra = new CellRangeAddress(
                        cell.getRowIndex() - (mr[1] - mr[0]), cell.getRowIndex(),
                        cell.getColumnIndex(), cell.getColumnIndex());
                writeSheetHolder.getSheet().addMergedRegionUnsafe(cra);
                break;
            }
        }
    }
}
