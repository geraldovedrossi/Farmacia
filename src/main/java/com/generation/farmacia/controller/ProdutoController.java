package com.generation.farmacia.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.farmacia.model.Produto;
import com.generation.farmacia.repository.CategoriaRepository;
import com.generation.farmacia.repository.ProdutoRepository;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProdutoController {

	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@GetMapping
	public ResponseEntity<List<Produto>> findAllProduto() {
		return ResponseEntity.ok(produtoRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Produto> findByIdProduto(@PathVariable Long id) {
		return produtoRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<Produto>> findByNomeProduto (@PathVariable String nome){
		return ResponseEntity.ok(produtoRepository.findAllByNomeContainingIgnoreCase(nome));
	}
	
	@GetMapping("/nomelab/{nome}~{laboratorio}")
	public ResponseEntity<List<Produto>> findByNomeLaboratorioProduto (@PathVariable String nome, @PathVariable String laboratorio){
		return ResponseEntity.ok(produtoRepository.findAllByNomeContainingIgnoreCaseAndLaboratorioContainingIgnoreCase(nome,laboratorio));
	}
	
	@GetMapping("/maior/{preco}")
	public ResponseEntity<List<Produto>> findByMaiorQue (@PathVariable BigDecimal preco){
		return ResponseEntity.ok(produtoRepository.findAllByPrecoGreaterThanOrderByPreco(preco));
	}
	
	@GetMapping("/menor/{preco}")
	public ResponseEntity<List<Produto>> findByMenorQue (@PathVariable BigDecimal preco){
		return ResponseEntity.ok(produtoRepository.findAllByPrecoLessThanOrderByPreco(preco));
	}
	
	@GetMapping("/entre/{preco1}~{preco2}")
	public ResponseEntity<List<Produto>> findByEntre (@PathVariable BigDecimal preco1, @PathVariable BigDecimal preco2){
		return ResponseEntity.ok(produtoRepository.findAllByPrecoBetweenOrderByPreco(preco1, preco2));
	}
	
	@PostMapping
	public ResponseEntity<Produto> postProduto(@Valid @RequestBody Produto produto) {
		if(categoriaRepository.existsById(produto.getCategoria().getId()))
		return ResponseEntity.status(HttpStatus.CREATED).body(produtoRepository.save(produto));
		
		return ResponseEntity.badRequest().build();
	}
	
	@PutMapping
	public ResponseEntity<Produto> putProduto(@Valid @RequestBody Produto produto) {
		if(categoriaRepository.existsById(produto.getCategoria().getId())) {
			return produtoRepository.findById(produto.getId())
					.map(resposta -> ResponseEntity.status(HttpStatus.OK).body(produtoRepository.save(produto)))
					.orElse(ResponseEntity.notFound().build());
		}
		return ResponseEntity.badRequest().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteProduto(@PathVariable Long id) {
		return produtoRepository.findById(id).map(resposta -> {
				produtoRepository.deleteById(id);
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();})
				.orElse(ResponseEntity.notFound().build());
				
	}
}
