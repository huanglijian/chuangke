package cn.ck.mapper;

import cn.ck.entity.Jobuser;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2018-09-21
 */
public interface JobuserMapper extends BaseMapper<Jobuser> {
      public Jobuser selectByUserId(String userId);
}
