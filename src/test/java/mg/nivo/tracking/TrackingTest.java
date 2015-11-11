package mg.nivo.tracking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.naming.NamingException;

import mg.nivo.tracking.spring.SpringAppInitializer;
import mg.nivo.tracking.spring.SpringConfig;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringConfig.class,
		SpringAppInitializer.class })
public class TrackingTest {
	private MockMvc mockMvc;

	@BeforeClass
	public static void setupClass() throws IllegalStateException,
			NamingException {
		SimpleNamingContextBuilder builder = SimpleNamingContextBuilder
				.emptyActivatedContextBuilder();
		SpringTestUtils.installContext(builder);
		builder.activate();
	}

	@Before
	public void setup() throws NamingException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Autowired
	private WebApplicationContext wac;

	@Test
	public void testLatest() throws Exception {
		ResultActions ret = mockMvc
				.perform(
						get("/tracking/testClient/latest").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"));
		System.out.println(ret.andReturn().getResponse().getContentAsString());
	}
}
