package com.liberty.exchange.service.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.liberty.exchange.entity.pojo.db.Query;
import com.liberty.exchange.entity.pojo.db.TableData;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 4:12
 * Description: MybatisBaseService
 */
public class MybatisBaseService<M extends Mapper<T>, T> {
    //此处必须用 @Autowired 不能用@Resource
    @Autowired
    protected M mapper;

    public void setMapper(M mapper) {
        this.mapper = mapper;
    }

    /**
     * 根据实体查询一条对应数据
     *
     * @param entity 实体
     * @return 结果
     */
    public T selectOne(T entity) {
        return mapper.selectOne(entity);
    }

    /**
     * 根据example更新
     *
     * @param entity  entity
     * @param example example
     * @return int
     */
    public int updateByExampleSelective(T entity, Example example) {
        return mapper.updateByExampleSelective(entity, example);
    }

    /**
     * 根据主键判断数据是否存在
     *
     * @param key 主键
     * @return 是否存在
     */
    public boolean existsWithPrimaryKey(Object key) {
        return mapper.existsWithPrimaryKey(key);
    }

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 结果
     */
    public T selectById(Object id) {
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 根据实体类条件查询list
     *
     * @param entity 实体
     * @return 结果
     */
    public List<T> selectList(T entity) {
        return mapper.select(entity);
    }

    /**
     * 查询所有记录
     *
     * @return 结果
     */
    public List<T> selectListAll() {
        return mapper.selectAll();
    }

    /**
     * 查询数量
     *
     * @param entity 实体
     * @return 结果
     */
    public Long selectCount(T entity) {
        return (long) mapper.selectCount(entity);
    }

    /**
     * 插入数据
     *
     * @param entity 实体
     */
    public int insert(T entity) {
        return mapper.insert(entity);
    }

    /**
     * 插入一条数据,只插入不为null的字段,不会影响有默认值的字段
     *
     * @param entity 实体
     */
    public int insertSelective(T entity) {
        return mapper.insertSelective(entity);
    }

    /**
     * 删除数据
     *
     * @param entity 实体
     */
    public int delete(T entity) {
        return mapper.delete(entity);
    }

    /**
     * 根据主键删除
     *
     * @param id 主键
     */
    public int deleteById(Object id) {
        return mapper.deleteByPrimaryKey(id);
    }


    /**
     * 更新实体 ,注入的字段全部更新（不判断是否为Null）
     *
     * @param entity 实体
     */
    public int updateById(T entity) {
        return mapper.updateByPrimaryKey(entity);
    }

    /**
     * 对字段进行判断再更新(如果为Null就忽略更新)
     *
     * @param entity 实体
     */
    public int updateSelectiveById(T entity) {
        return mapper.updateByPrimaryKeySelective(entity);

    }

    /**
     * 根据条件查询 相当于like,order by等操作
     *
     * @param example 条件  tk.mybatis.mapper.entity.Example
     * @return 实体
     */
    public List<T> selectByExample(Object example) {
        return mapper.selectByExample(example);
    }

    /**
     * 根据条件查询 相当于like,order by等操作
     *
     * @param example 条件  tk.mybatis.mapper.entity.Example
     * @return count
     */
    public int selectCountByExample(Object example) {
        return mapper.selectCountByExample(example);
    }

    /**
     * 分页
     *
     * @param query 条件
     * @return 实体
     */
    @SuppressWarnings("unchecked")
    public TableData<T> selectByQuery(Query query) {
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Example example = new Example(clazz);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("1=1");
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            if (null != entry.getValue())
                criteria.andEqualTo(entry.getKey(), (String) entry.getValue());
        }
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<T> list = mapper.selectByExample(example);
        return new TableData<T>(result.getTotal(), list);
    }

    @SuppressWarnings("unchecked")
    public TableData<T> selectByQueryAndExample(Query query, Example example) {
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("1=1");
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            if (null != entry.getValue())
                criteria.andEqualTo(entry.getKey(), (String) entry.getValue());
        }
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<T> list = mapper.selectByExample(example);
        return new TableData<T>(result.getTotal(), list);
    }
}

