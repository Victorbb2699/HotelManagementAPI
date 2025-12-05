package es.twd.hotel.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import es.twd.hotel.service.HotelService;

@SpringBootTest
@AutoConfigureMockMvc
class HotelControllerSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private HotelService hotelService;

	@Test
	void whenNoAuth_thenUnauthorized() throws Exception {
		mockMvc.perform(get("/hotels")).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void whenUserAuth_thenGetHotelsOk() throws Exception {
		mockMvc.perform(get("/hotels")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void whenUserDeletesHotel_thenForbidden() throws Exception {
		mockMvc.perform(delete("/hotels/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void whenAdminDeletesHotel_thenOk() throws Exception {
		mockMvc.perform(delete("/hotels/1")).andExpect(status().isNoContent());
	}

}
