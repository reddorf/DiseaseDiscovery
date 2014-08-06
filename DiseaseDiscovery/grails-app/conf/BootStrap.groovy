import diseaseDiscovery.domain.auth.Role
import diseaseDiscovery.domain.auth.User
import diseaseDiscovery.domain.auth.UserRole

class BootStrap {

    def init = { servletContext ->
		println "Setting admin user..."
		
		if(!User.findByUsername("admin")){
			def admin = new User(username: 'admin', password: '1234').save()
			def adminRole = new Role(authority: 'ROLE_ADMIN').save()
			def adminToRole = new UserRole(user: admin, role: adminRole).save(flush:true)
		}
		println "Admin created"
    }
    def destroy = {
    }
}
