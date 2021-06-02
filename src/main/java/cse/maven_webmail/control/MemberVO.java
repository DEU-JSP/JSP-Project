package cse.maven_webmail.control;

public final class MemberVO {
    /**
     * 필요한 property 선언
     */
    private String id;
    private String name;
    private String tel;
    private String birth;
    private String addr;
    private String memo;


    public MemberVO() {
    }

    public MemberVO(final String id, final String name, final String tel, final String birth, final String addr, final String memo) {

        this.id = id;
        this.name = name;
        this.tel = tel;

        this.birth = birth;
        this.addr = addr;
        this.memo = memo;

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
