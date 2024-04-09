package io.github.luoyikuan.easyexcel;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.compress.utils.Sets;
import org.junit.jupiter.api.Test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;

import io.github.luoyikuan.easyexcel.merge.ExcelId;
import io.github.luoyikuan.easyexcel.merge.MergeStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 合并测试
 * 
 * @author lyk
 */
public class MergeTests {

    @Test
    public void testMerge() {
        List<StudentScore> list = new LinkedList<>();

        list.add(new StudentScore(1L, "张三", "数学", 100));
        list.add(new StudentScore(1L, "张三", "语文", 99));
        list.add(new StudentScore(1L, "张三", "英语", 98));

        list.add(new StudentScore(2L, "李四", "数学", 97));
        list.add(new StudentScore(2L, "李四", "语文", 96));
        list.add(new StudentScore(2L, "李四", "英语", 95));

        list.add(new StudentScore(3L, "王五", "数学", 94));
        list.add(new StudentScore(3L, "王五", "语文", 93));

        list.add(new StudentScore(4L, "老六", "数学", 92));

        EasyExcel
                .write("./merge.xlsx")
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerWriteHandler(new MergeStrategy<>(list, Sets.newHashSet(0, 1)))
                .head(StudentScore.class)
                .sheet("合并测试")
                .doWrite(list);
    }

}

/**
 * 学习成绩
 */
@Getter
@AllArgsConstructor
class StudentScore {

    @ExcelId
    @ExcelProperty("学生ID")
    private Long studentId;

    @ExcelProperty("学生姓名")
    private String studentName;

    @ExcelProperty("考试科目")
    private String subject;

    @ExcelProperty("考试成绩")
    private Integer score;
}
