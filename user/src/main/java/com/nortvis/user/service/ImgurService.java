package com.nortvis.user.service;

import com.nortvis.user.entity.User;
import com.nortvis.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class ImgurService {

    private final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";
    private final String IMGUR_CLIENT_ID = "af2c9502c5469d5";
    @Autowired
    private UserRepository userRepository;
    public String uploadImage(MultipartFile image) {
        RestTemplate restTemplate = new RestTemplate();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        // Prepare headers with the Imgur client ID
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Client-ID " + IMGUR_CLIENT_ID);

        // Prepare the image data to be sent in the request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new HttpEntity<>(image.getResource(), headers));
        try {
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    IMGUR_UPLOAD_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String responseBody = responseEntity.getBody();
                String imgurLink = extractImgurLinkFromResponse(responseBody,user);
                return imgurLink;
            } else {
                return "Error parsing Imgur response: " + responseEntity.getBody().toString();
            }
        }catch (HttpClientErrorException e) {
            return "Image upload to Imgur failed. Error: " + e.getResponseBodyAsString();
        }
    }
    public void deleteImage() {
        RestTemplate restTemplate = new RestTemplate();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        if(user.getImageKey()!=null){
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Client-ID " + IMGUR_CLIENT_ID);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Add the image ID to the URL
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(IMGUR_UPLOAD_URL+"/"+user.getImageKey());

            // Send an HTTP DELETE request to delete the image
            restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
        }
        user.setImageKey(null);
        user.setImageUrl(null);
        userRepository.save(user);
        // Set the required headers for authorization

    }
    private String extractImgurLinkFromResponse(String responseBody,User user) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseNode = objectMapper.readTree(responseBody);
            System.out.println(responseBody.toString());
            String imgurLink = responseNode.get("data").get("link").asText();
            String imageId = responseNode.get("data").get("deletehash").asText();
            user.setImageKey(imageId);
            user.setImageUrl(imgurLink);
            userRepository.save(user);
            return imgurLink;
        } catch (Exception e) {
            return "Error parsing Imgur response: " + e.getMessage();
        }
    }
}
