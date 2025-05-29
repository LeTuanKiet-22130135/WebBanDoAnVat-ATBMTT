document.addEventListener('DOMContentLoaded', function () {
    const variantSelect = document.getElementById('variant-select');
    const priceDisplay = document.getElementById('product-price');
    const addToCartForm = document.getElementById('add-to-cart-form');
    const selectedVariantIdInput = document.getElementById('selected-variant-id');

    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    function updateVariantInfo() {
        if (!variantSelect) {
            console.error("Variant select element not found!");
            return;
        }

        const selectedOption = variantSelect.options[variantSelect.selectedIndex];
        if (!selectedOption) {
            console.error("No option selected in variant dropdown!");
            return;
        }

        const price = parseFloat(selectedOption.getAttribute('data-price'));
        const variantId = selectedOption.value;

        if (priceDisplay) {
            priceDisplay.textContent = formatCurrency(price);
        }

        // Update the hidden input with the selected variant ID
        if (selectedVariantIdInput) {
            selectedVariantIdInput.value = variantId;
            console.log("Set selectedVariantIdInput.value to: " + variantId);
        } else {
            console.error("selectedVariantIdInput not found!");
        }
    }

    if (variantSelect) {
        variantSelect.addEventListener('change', updateVariantInfo);

        // Set initial values based on the first variant
        updateVariantInfo();

        // Add a submit event listener to the form to ensure variantId is set
        if (addToCartForm) {
            addToCartForm.addEventListener('submit', function(event) {
                // Update the variant ID one more time before submitting
                updateVariantInfo();
                console.log("Form submitted with variantId: " + selectedVariantIdInput.value);
            });
        }
    } else {
        console.error("Variant select element not found on page load!");
    }
});
