package timechat.fcgz.sj.time.ui.file;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

/**
 * 用户信息
 * 
 * @author zyzhang
 * @date 2016-1-29 上午9:50:18
 */
public class UserInfoEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nickname;
	private String age;
	private String sex;
	private String photoname;
	private String userbusiness;
	private String usercompany;
	private String userprofession;
	private String usermotto;
	private String homeaddress;
	private String companyaddress;
	private String commonaddress;
	private Drawable uPhoto = null;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhotoname() {
		return photoname;
	}

	public void setPhotoname(String photoname) {
		this.photoname = photoname;
	}

	public String getUserbusiness() {
		return userbusiness;
	}

	public void setUserbusiness(String userbusiness) {
		this.userbusiness = userbusiness;
	}

	public String getUsercompany() {
		return usercompany;
	}

	public void setUsercompany(String usercompany) {
		this.usercompany = usercompany;
	}

	public String getUserprofession() {
		return userprofession;
	}

	public void setUserprofession(String userprofession) {
		this.userprofession = userprofession;
	}

	public String getUsermotto() {
		return usermotto;
	}

	public void setUsermotto(String usermotto) {
		this.usermotto = usermotto;
	}

	public String getHomeaddress() {
		return homeaddress;
	}

	public void setHomeaddress(String homeaddress) {
		this.homeaddress = homeaddress;
	}

	public String getCompanyaddress() {
		return companyaddress;
	}

	public void setCompanyaddress(String companyaddress) {
		this.companyaddress = companyaddress;
	}

	public String getCommonaddress() {
		return commonaddress;
	}

	public void setCommonaddress(String commonaddress) {
		this.commonaddress = commonaddress;
	}

	public Drawable getuPhoto() {
		return uPhoto;
	}

	public void setuPhoto(Drawable uPhoto) {
		this.uPhoto = uPhoto;
	}

}
