package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Info;

/**
 * 用户信息 数据层
 *
 * @author ruoyi
 */
public interface InfoMapper {

    /**
     * 查询用户信息
     *
     * @param info 用户信息
     * @return 用户信息
     */
    public Info selectInfo(Info info);

    /**
     * 通过ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public Info selectInfoById(Long id);

    /**
     * 查询用户信息列表
     *
     * @param info 用户信息
     * @return 用户信息集合
     */
    public List<Info> selectInfoList(Info info);

    /**
     * 新增用户信息
     *
     * @param info 用户信息
     * @return 结果
     */
    public int insertInfo(Info info);

    /**
     * 修改用户信息
     *
     * @param info 用户信息
     * @return 结果
     */
    public int updateInfo(Info info);

    /**
     * 删除用户信息
     *
     * @param id 用户ID
     * @return 结果
     */
    public int deleteInfoById(Long id);

    /**
     * 批量删除用户信息
     *
     * @param ids 需要删除的用户ID
     * @return 结果
     */
    public int deleteInfoByIds(Long[] ids);

//    void insertImage(String fileName);

    void updateInfoAvatar(String fileName);
}