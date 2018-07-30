package bikelong.iot2.goott.bikelong;

import android.app.Application;

import java.io.Serializable;

public class Member implements Serializable{

    private static Member member ;
    private final MemberVo memberVo;

    private Member() {
        memberVo = new MemberVo();
    }

    public static Member getInstance(){
        if(member == null){
            member = new Member();
        }
        return member;
    }

    public MemberVo getMemberVo() {
        return memberVo;
    }

    public void fillMemberVo(MemberVo memberVo) {
        this.memberVo.setId(memberVo.getId());
        this.memberVo.setName(memberVo.getName());
        this.memberVo.setPassword(memberVo.getPassword());
        this.memberVo.setPhone(memberVo.getPhone());
        this.memberVo.setWeight(memberVo.getWeight());
        this.memberVo.setAddress(memberVo.getAddress());
        this.memberVo.setPoint(memberVo.getPoint());
    }

    public String getId(){
        return memberVo.getId();
    }
    public String getName(){
        return memberVo.getName();
    }
    public String getPassword(){
        return memberVo.getPassword();
    }
    public String getPhone(){
        return memberVo.getPhone();
    }
    public int getWeight(){
        return memberVo.getWeight();
    }
    public String getAddress(){
        return memberVo.getAddress();
    }
    public int getPoint(){
        return memberVo.getPoint();
    }

    class MemberVo implements Serializable {
        private String id;
        private String name;
        private String password;
        private String phone;
        private int weight;
        private String address;
        private int point;

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
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
        public String getPhone() {
            return phone;
        }
        public void setPhone(String phone) {
            this.phone = phone;
        }
        public int getWeight() {
            return weight;
        }
        public void setWeight(int weight) {
            this.weight = weight;
        }
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        public int getPoint() {
            return point;
        }
        public void setPoint(int point) {
            this.point = point;
        }
    }

}
