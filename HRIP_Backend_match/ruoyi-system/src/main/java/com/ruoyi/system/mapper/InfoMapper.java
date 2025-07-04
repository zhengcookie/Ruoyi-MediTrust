package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Info;

/**
 * 用户信息 数据层
 *
 * @author ruoyi
 */
<<<<<<< HEAD
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
=======
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
>>>>>>> ceece8c (实现多选删除功能)
     *
     * @param id 用户ID
     * @return 用户信息
     */
<<<<<<< HEAD
    public Info selectUserById(Long id);

    /**
     * 通过用户ID删除用户信息
=======
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
>>>>>>> ceece8c (实现多选删除功能)
     *
     * @param id 用户ID
     * @return 结果
     */
<<<<<<< HEAD
    public int deleteUserById(Long id);
=======
    public int deleteInfoById(Long id);
>>>>>>> ceece8c (实现多选删除功能)

    /**
     * 批量删除用户信息
     *
     * @param ids 需要删除的用户ID
     * @return 结果
     */
<<<<<<< HEAD
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
=======
    public int deleteInfoByIds(Long[] ids);

//    void insertImage(String fileName);

    void updateInfoAvatar(String fileName);
}
>>>>>>> ceece8c (实现多选删除功能)
