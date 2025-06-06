/**
 * JavaScript for handling AJAX cart operations
 */

// Global function to add product to cart via AJAX
function addToCart(productId, variantId, quantity) {
    $.ajax({
        url: 'cart',
        type: 'POST',
        data: {
            productId: productId,
            variantId: variantId,
            quantity: quantity,
            action: 'add'
        },
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        success: function(response) {
            if (response.success) {
                // Update cart count in header
                updateCartCount(response.cartItemCount);
                // No success message popup as per requirement
            } else {
                console.error('Error: ' + response.message);
            }
        },
        error: function() {
            console.error('An error occurred while adding the product to cart.');
        }
    });
}

// Global function to update cart count in header
function updateCartCount(count) {
    // Update cart count in mobile view
    $('.d-inline-flex.align-items-center.d-block.d-lg-none .fa-shopping-cart').next('span').text(count);

    // Update cart count in desktop view
    $('.d-flex.align-items-center .fa-shopping-cart').next('span').text(count);
}

// Function to initialize product detail page functionality
function initProductDetailPage() {
    // Handle Add to Cart button click
    $('#add-to-cart-btn').click(function() {
        const productId = $('#product-id').val();
        let variantId = $('#selected-variant-id').val();
        const quantity = $('#quantity').val();

        // Check if variantId is valid (not null, undefined, empty, or 0)
        if (!variantId || variantId === '0' || variantId === 0) {
            // Get the first variant ID for the product
            $.ajax({
                url: 'getFirstVariant',
                type: 'GET',
                data: {
                    productId: productId
                },
                success: function(response) {
                    if (response.success) {
                        // Call the addToCart function with the first variant ID
                        addToCart(productId, response.variantId, quantity);
                    } else {
                        console.error('Error: ' + response.message);
                    }
                },
                error: function() {
                    console.error('An error occurred while getting the variant ID.');
                }
            });
        } else {
            // Call the addToCart function with the selected variant ID
            addToCart(productId, variantId, quantity);
        }
    });

    // Handle quantity buttons
    $('.btn-plus').click(function() {
        var quantity = parseInt($('#quantity').val());
        $('#quantity').val(quantity + 1);
    });

    $('.btn-minus').click(function() {
        var quantity = parseInt($('#quantity').val());
        if (quantity > 1) {
            $('#quantity').val(quantity - 1);
        }
    });
}

// Function to initialize product listing page functionality
function initProductListingPage() {
    // Handle Add to Cart button click using event delegation
    // This works for both static and dynamically loaded content
    $(document).on('click', '.add-to-cart-btn', function() {
        const productId = $(this).data('product-id');
        const quantity = 1; // Default to 1

        // Get the first variant ID for the product
        $.ajax({
            url: 'getFirstVariant',
            type: 'GET',
            data: {
                productId: productId
            },
            success: function(response) {
                if (response.success) {
                    // Call the addToCart function with the first variant ID
                    addToCart(productId, response.variantId, quantity);
                } else {
                    console.error('Error: ' + response.message);
                }
            },
            error: function() {
                console.error('An error occurred while getting the variant ID.');
            }
        });
    });
}

$(document).ready(function() {

    // Function to update cart items via AJAX
    function updateCartItem(productId, variantId, action) {
        $.ajax({
            url: 'UpdateCartServlet',
            type: 'POST',
            data: {
                productId: productId,
                variantId: variantId,
                action: action
            },
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            success: function(response) {
                if (response.success) {
                    // Reload cart items fragment
                    reloadCartItems();
                    // Update cart summary
                    updateCartSummary(response.cartSubtotal);
                    // Update cart count in header
                    updateCartCount(response.cartItemCount);
                } else {
                    alert('Error: ' + response.message);
                }
            },
            error: function() {
                alert('An error occurred while updating the cart.');
            }
        });
    }

    // Function to remove item from cart via AJAX
    function removeCartItem(productId, variantId) {
        $.ajax({
            url: 'RemoveFromCartServlet',
            type: 'GET',
            data: {
                productId: productId,
                variantId: variantId
            },
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            success: function(response) {
                if (response.success) {
                    // Reload cart items fragment
                    reloadCartItems();
                    // Update cart summary
                    updateCartSummary(response.cartSubtotal);
                    // Update cart count in header
                    updateCartCount(response.cartItemCount);
                } else {
                    alert('Error: ' + response.message);
                }
            },
            error: function() {
                alert('An error occurred while removing the item from cart.');
            }
        });
    }

    // Function to reload cart items fragment
    function reloadCartItems() {
        $('#cart-items-container').load('cart #cart-items-container > *', function() {
            // Reattach event handlers after content is loaded
            attachEventHandlers();
        });
    }

    // Function to attach event handlers to cart buttons
    function attachEventHandlers() {
        // Increase quantity button
        $('.cart-update-btn').off('click').on('click', function() {
            const productId = $(this).data('product-id');
            const variantId = $(this).data('variant-id');
            const action = $(this).data('action');
            updateCartItem(productId, variantId, action);
        });

        // Remove item button
        $('.cart-remove-btn').off('click').on('click', function() {
            const productId = $(this).data('product-id');
            const variantId = $(this).data('variant-id');
            removeCartItem(productId, variantId);
        });
    }

    // Function to update cart summary
    function updateCartSummary(cartSubtotal) {
        // Format currency with Vietnamese Dong symbol
        function formatCurrency(amount) {
            return new Intl.NumberFormat('vi-VN', {
                style: 'decimal',
                minimumFractionDigits: 0,
                maximumFractionDigits: 2
            }).format(amount) + ' ₫';
        }

        // Update subtotal
        $('.col-lg-4 .border-bottom .d-flex:first-child h6:last-child').text(formatCurrency(cartSubtotal));

        // Get shipping cost
        const shippingCostText = $('.col-lg-4 .border-bottom .d-flex:last-child h6:last-child').text();
        const shippingCost = parseFloat(shippingCostText.replace(/[^\d.-]/g, ''));

        // Calculate and update total
        const subtotal = parseFloat(cartSubtotal);
        const total = subtotal + shippingCost;
        $('.col-lg-4 .pt-2 .d-flex h5:last-child').text(formatCurrency(total));
    }

    // Initial attachment of event handlers
    attachEventHandlers();
});
