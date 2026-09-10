package com.sareeaura.user.service;

import com.sareeaura.auth.service.AuthService;
import com.sareeaura.exception.ResourceNotFoundException;
import com.sareeaura.user.dto.AddressRequest;
import com.sareeaura.user.dto.AddressResponse;
import com.sareeaura.user.entity.Address;
import com.sareeaura.user.entity.User;
import com.sareeaura.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AuthService authService;

    public List<AddressResponse> getUserAddresses() {
        User user = authService.getCurrentUser();
        return addressRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse addAddress(AddressRequest request) {
        User user = authService.getCurrentUser();

        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry() != null ? request.getCountry() : "India")
                .addressType(request.getAddressType())
                .isDefault(request.isDefault())
                .build();

        return mapToResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(Long id) {
        User user = authService.getCurrentUser();
        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        addressRepository.delete(address);
    }

    public AddressResponse mapToResponse(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .fullName(a.getFullName())
                .phone(a.getPhone())
                .addressLine1(a.getAddressLine1())
                .addressLine2(a.getAddressLine2())
                .city(a.getCity())
                .state(a.getState())
                .postalCode(a.getPostalCode())
                .country(a.getCountry())
                .addressType(a.getAddressType())
                .isDefault(a.isDefault())
                .build();
    }
}
