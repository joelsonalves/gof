package com.joelsonalves.gof.service.impl;

import com.joelsonalves.gof.model.Cliente;
import com.joelsonalves.gof.model.Endereco;
import com.joelsonalves.gof.model.repository.ClienteRepository;
import com.joelsonalves.gof.model.repository.EnderecoRepository;
import com.joelsonalves.gof.service.ClienteService;
import com.joelsonalves.gof.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ViaCepService viaCepService;

    private void salvarClienteComCep(Cliente cliente) {

        String cep = cliente.getEndereco().getCep();
        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {

            Endereco novoEndereco = viaCepService.consultarCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;

        });
        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);

    }

    @Override
    public Iterable<Cliente> buscarTodos() {

        return clienteRepository.findAll();

    }

    @Override
    public Cliente buscarPorId(Long id) {

        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.get();

    }

    @Override
    public void inserir(Cliente cliente) {

        salvarClienteComCep(cliente);

    }

    @Override
    public void atualizar(Long id, Cliente cliente) {

        Optional<Cliente> clienteDB = clienteRepository.findById(id);

        if (clienteDB.isPresent()) {

            cliente.setId(id);

            if (cliente.getNome() == null || cliente.getNome().equals("")) cliente.setNome(clienteDB.get().getNome());

            if (cliente.getEndereco() == null || cliente.getEndereco().getCep() == null || cliente.getEndereco().getCep().equals("")) cliente.setEndereco(clienteDB.get().getEndereco());

            salvarClienteComCep(cliente);

        }

    }

    @Override
    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }
}
