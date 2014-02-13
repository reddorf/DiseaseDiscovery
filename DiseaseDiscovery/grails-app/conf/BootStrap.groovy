import auth.Role
import auth.User
import auth.UserRole

class BootStrap {

    def init = { servletContext ->
		def admin = new User(username: 'admin', password: '1234').save()
		def adminRole = new Role(authority: 'ROLE_ADMIN').save()
		def adminToRole = new UserRole(user: admin, role: adminRole).save()
    }
    def destroy = {
    }
}
