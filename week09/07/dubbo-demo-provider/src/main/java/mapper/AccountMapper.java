package mapper;

import entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author 13041285
 * @Description TODO
 * @date 2021/7/5-17:31
 */
@Mapper
@Repository
public interface AccountMapper {
    /**
     * pay for money
     * @param account account
     */
    @Update("update `himly_dubbo_account` set us_account = us_account + #{us_account}, rmb_account = rmb_account + " +
            "#{rmb_account} where us_account >= #{us_account} and rmb_account >= #{rmb_account} and id = #{id}")
    boolean payment(Account account);

    /**
     * query one
     * @param account account
     * @return account
     */
    @Select("select * from himly_dubbo_account where id = #{id}")
    Account queryOne(Account account);
}
