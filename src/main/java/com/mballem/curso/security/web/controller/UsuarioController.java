package com.mballem.curso.security.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("u")
public class UsuarioController {
	
	@Autowired
	UsuarioService usuarioService;

	// abrir pagina de dados pessoais de medicos 
	@GetMapping({"/novo/cadastro/usuario"})
	public String cadastroPorAdminParaAdminMedicoPaciente(Usuario usuario) {	
		
		return "usuario/cadastro";
	}
	
	//abrir lista de usuários
	@GetMapping("/lista")
	public String listarUsuarios() {	
		
		return "usuario/lista";
	}
	
	//abrir lista de usuários
	@GetMapping("/datatables/server/usuarios")
	public ResponseEntity<?> listarUsuariosDatatables(HttpServletRequest request) {	
		
		return ResponseEntity.ok(usuarioService.buscarTodos(request));
	}
	
	/**
	 * Método que recebe a requisição do formulário para cadastro de novos usuários no sistema.
	 * O método será utilizado quando um perfil do tipo ADMIN estiver logado. Com isso, novos usuários com perfis ADMIN e MEDICO poderão
	 * ser salvos.
	 * O único usuário que poderá fazer seu próprio cadastro será o PACIENTE.
	 * 
	 * @param usuario objeto do tipo Usuario que foi enviado pelo formulário
	 * @param attr Objeto para fazer o redirecionamento
	 * */
	@PostMapping("/cadastro/salvar")
	public String salvarUsuarios(Usuario usuario, RedirectAttributes attr) {
		List<Perfil> perfis = usuario.getPerfis();//pega a lista de perfis do novo usuário p-ara facilitar os testes a seguir
		
		if(
			perfis.size() > 2 //se a lista de perfis contiver mais de dois perfis, significa que um usuário está sendo cadastrado como sendo ADMIN, MEDICO e PACIENTE ao mesmo tempo. E as regras de negócio não permitem.
			|| perfis.contains(new Perfil(1L)) && perfis.contains(new Perfil(3L))//impede o cadastro de um usuário com perfil de ADMIN e PACIENTE
			|| perfis.contains(new Perfil(2L)) && perfis.contains(new Perfil(3L))//impede o cadastro de um usuário com perfil de MEDICO e PACIENTE
			) {
			
			attr.addFlashAttribute("falha", "paciente não pode ser ADMIN e/ou MEDICO");
			attr.addFlashAttribute("usuario", usuario );
		} 
		
		else { //Se todos os testes acima forem falsos, o cadastro poderá ser feito
			try {
				usuarioService.salvarUsuario(usuario);
				attr.addFlashAttribute("sucesso", "Usuário salvo com sucesso");
			} catch(DataIntegrityViolationException ex) {
				attr.addFlashAttribute("falha", "Email já cadastrado");
			}
		}
		
		return "redirect:/u/novo/cadastro/usuario";//redireciona para a próprio formulário de cadastro
	}
	
	/**
	 * Método que será chamado pelo botão editar presente na tela de lista de usuários cadastrados.
	 * O método não fará a edição do usuário, apenas buscará as informações para serem exibidas na tela de cadastro. 
	 * Com as informações na tela de cadastro, o usuario poderá ser editado e salvo com o método salvarUsuarios().
	 * O ModelAndView envia as informações do usuario a ser editado para a tela de cadastro(pre-edição)
	 * 
	 * @param id o id do usuário a ser editado
	 * @return ModelAndView o objeto ModelAndView é semelhante ao ResponseEntity. essa classe é utilizada para especificar a 
	 * view que será renderizada e quais os dados ela utilizará para isso.
	 * */
	@GetMapping("/editar/credenciais/usuario/{id}")
	public ModelAndView preEditarCredenciais(@PathVariable("id") Long id) {	
		
		/**
		 * O primeiro parametro indica a página que abrirá como resposta.
		 * usuario é a variável que enviará os dados do usuário para a página de cadastro
		 * o terceiro parametro é o objeto contando os dados do usuario
		 * */
		return new ModelAndView("usuario/cadastro", "usuario", usuarioService.buscarPorId(id));
	}
}
