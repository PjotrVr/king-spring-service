openEndpoint = (endpointName) => {
    window.open(endpointName, '_blank');
}

openProductById = () => {
    const productIdInput = document.getElementById('product-id');
    const productId = productIdInput.value;

    if (isNaN(productId) || productId.trim() === "") {
        alert("Please enter a valid numeric product ID.");
    } else {
        productIdInput.value = "";
        openEndpoint(`/products/${productId}`);
    }
}
