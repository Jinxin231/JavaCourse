package entity;

import lombok.Data;

/**
 * @author 13041285
 * @Description TODO
 * @date 2021/7/5-17:20
 */
@Data
public class Account {

    private Long id;

    private String name;

    private Long rmb_account;

    private Long us_account;
}
