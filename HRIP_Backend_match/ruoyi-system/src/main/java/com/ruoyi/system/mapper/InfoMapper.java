package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Info;

/**
 * 用户信息 数据层
 *
 * @author ruoyi
 */
public interface InfoMapper
{
    /**
     * 根据条件分页查询用户信息
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<Info> selectUserList(Info user);

    /**
     * 根据所有用户信息
     *
     * @return 用户信息集合信息
     */
    public List<Info> selectUserAll();

    /**
     * 根据用户ID查询信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public Info selectUserById(Long id);

    /**
     * 通过用户ID删除用户信息
     *
     * @param id 用户ID
     * @return 结果
     */
    public int deleteUserById(Long id);

    /**
     * 批量删除用户信息
     *
     * @param ids 需要删除的用户ID
     * @return 结果
     */
    public int deleteUserByIds(Long[] ids);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(Info user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(Info user);
}
