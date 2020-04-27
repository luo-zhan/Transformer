package com.robot.translator.core.translator;

import com.robot.translator.core.annotation.Dictionary;
import com.robot.translator.core.enums.FormatType;
import com.robot.translator.core.util.StringUtil;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 翻译默认实现
 * <p>
 * 适用于字典为单表，使用sql查询
 *
 * @author luozhan
 * @create 2020-04
 */
@Component
public class DefaultTranslator implements Translatable {
    @Resource
    JdbcTemplate template;

    @Override
    public String translate(String groupValue, String origin, Dictionary dictConfig, Class dictClass) {
        String codeColumn = dictConfig.codeColumn();
        String textColumn = dictConfig.textColumn();
        String groupColumn = dictConfig.groupColumn();
        String tableName = getTableName(dictConfig, dictClass);

        Assert.isTrue(!StringUtils.isEmpty(codeColumn), "@Dictionary注解codeColumn配置有误，找不到指定的属性名，class:" + dictClass.getSimpleName());
        Assert.isTrue(!StringUtils.isEmpty(textColumn), "@Dictionary注解textColumn配置有误，找不到指定的属性名，class:" + dictClass.getSimpleName());

        // 使用sql查询
        String sql = "select %s from %s where %s = ? ";
        Object[] params = new Object[]{origin};

        if (!StringUtils.isEmpty(groupColumn)) {
            sql = sql + " and %s = ?";
            params = new Object[]{origin, groupValue};
        }


        sql = String.format(sql, textColumn, tableName, codeColumn, groupColumn);
        // queryForObject查不到记录会报错，所以使用queryForList方法
        return DataAccessUtils.singleResult(template.queryForList(sql, params, String.class));
    }

    private String getTableName(Dictionary dictConfig, Class dictClass) {
        if (StringUtils.isEmpty(dictConfig.table())) {
            String className = dictClass.getSimpleName();
            return className.substring(0, 1) + StringUtil.parseCamelTo(className.substring(1), FormatType.UPPERCASE_UNDERLINE);
        }
        return dictConfig.table();
    }

}
